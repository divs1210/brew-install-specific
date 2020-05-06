(ns brew-install-specific.core
  (:gen-class)
  (:require [clojure.string :as str]
            [clansi :refer [style]]
            [clj-jgit.porcelain :as git])
  (:import [java.net URI]))

(defonce homebrew-repo-path
  "/usr/local/Homebrew/Library/Taps/homebrew/homebrew-core/")

(defonce commit-url*
  "https://github.com/Homebrew/homebrew-core/commit/%s")

(defonce install-url*
  "https://raw.githubusercontent.com/Homebrew/homebrew-core/%s/Formula/%s.rb")

(defn -main
  [& [package-at-version]]
  (Thread/setDefaultUncaughtExceptionHandler
   (reify Thread$UncaughtExceptionHandler
     (uncaughtException [_ _ ex]
       (println (style (.getMessage ex) :red)))))
  (if-not (string? package-at-version)
    (println "Usage: brew-install-specific package@version")
    (let [[package version] (->> package-at-version
                                 (partition-by #(= \@ %))
                                 ((fn [[p _ v]]
                                    [p v]))
                                 (map str/join))]
      (if-not (and (seq package)
                   (seq version))
        (println "Usage: brew-install-specific package@version")
        (let [repo (git/load-repo homebrew-repo-path)
              matching-commits (->> (git/git-log repo)
                                    (filter #(and (str/includes? (:msg %) package)
                                                  (str/includes? (:msg %) version))))]
          (if (empty? matching-commits)
            (println (style "No matching versions found." :red))
            (do
              (println (style "Matching versions:" :yellow))
              (doseq [[idx {:keys [^org.eclipse.jgit.revwalk.RevCommit id
                                   ^String msg]}]
                      (map-indexed vector matching-commits)

                      :let [idx (inc idx)
                            sha (.getName id)
                            url (format commit-url* sha)]]
                (print (style (str idx ". ") :yellow))
                (println (style msg :green))
                (print (-> idx str count (+ 2) (repeat " ") str/join))
                (println (style url :yellow :underline)))

              (print (style "\nSelect index: " :yellow))
              (flush)
              (let [sel-text (read-line)
                    sel-idx (Integer/valueOf ^String sel-text)
                    sel-item (nth matching-commits (dec sel-idx))]
                (println (style "Run:\n  brew install" :yellow)
                         (style (format install-url*
                                        (.getName ^org.eclipse.jgit.revwalk.RevCommit
                                                  (sel-item :id))
                                        package)
                                :green :underline))))))))))
