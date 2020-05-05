(ns brew-install-specific.core
  (:gen-class)
  (:require [clojure.string :as str]
            [clojure.pprint :refer [pprint]]
            [clj-http.client :as http]
            [net.cgrand.enlive-html :as html]))

(defonce commits-url*
  "https://github.com/Homebrew/homebrew-core/commits/master/Formula/%s.rb")

(defonce commit-url*
  "https://github.com/Homebrew/homebrew-core/tree/%s/Formula/%s.rb")

(defn fetch-versions
  [package version data]
  (let [i (atom 0)]
    (for [commit-title-element (html/select data [:p.commit-title :a])
          :let [{:keys [aria-label href]} (:attrs commit-title-element)
                commit-msg aria-label
                commit-sha (-> href
                               (str/replace-first "/Homebrew/homebrew-core/commit/" "")
                               (str/split #"#diff")
                               first)]
          :when (str/includes? commit-msg version)]
      {:id (swap! i inc)
       :msg commit-msg
       :sha commit-sha
       :url (format commit-url* commit-sha package)})))

(defn -main
  [& [package-at-version]]
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
        (let [commits-uri (-> commits-url*
                              (format package)
                              java.net.URI.)
              data (html/html-resource commits-uri)
              versions (fetch-versions package version data)]
          (println "Versions:")
          (doseq [{:keys [id msg url]} (sort-by :id versions)]
            (println (str id ". " msg))
            (print (-> id str count (+ 2) (repeat " ") str/join))
            (println url "\n")))))))
