(ns brew-install-specific.core
  (:gen-class)
  (:require [clojure.string :as str]))

(defonce formula-url
  "https://github.com/Homebrew/homebrew-core/blob/master/Formula/%s.rb")

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
        (do
          (println "Fetching versions for:" package
                   "\nFrom:" (format formula-url package))
          )))))
