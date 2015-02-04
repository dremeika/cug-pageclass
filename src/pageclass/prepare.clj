(ns pageclass.prepare
  (:require [clojure.string :as s]
            [clojure.tools.logging :as log]
            [pageclass.core :as core]
            [pageclass.http :as http]
            [pageclass.features :as feat])
  (:import [java.util UUID]))

(def data-rec-format "%s\t%s\t%s\n")
(def dataset-file "dataset/data.tsv")
(def page-file-format "dataset/data/%s.html")

(defn- fetch-page [id url]
  (log/debugf "Fetching '%s'" url)
  (let [page-file (format page-file-format id)
        page (http/fetch url)]
    (log/debugf "Got status '%s' for url '%s'" (:status page) url)
    (spit page-file (:body page))))

(defn add-page
  "Add page to training set (dataset/data.tsv and data folder)"
  [label url]
  (let [id (str (UUID/randomUUID))]
    (fetch-page id url)
    (spit dataset-file
          (format data-rec-format label id url)
          :append true)
    (log/debugf "Added '%s' '%s' '%s'" label id url)))

(defn- load-data
  "Loads data from dataset file and returns as [class id url] triples"
  []
  (->> dataset-file
       (slurp)
       (s/split-lines)
       (map #(s/split % #"\t"))))

(defn generate-features
  "Generates features for dataset and writes to provided file"
  [out-file]
  (letfn [(calculate-features
            [[class id url]]
            (let [html (slurp (format page-file-format id))]
              (cons class (feat/generate-features url html))))
          (format-features
            [features]
            (s/join "\t" features))]

    (let [data (load-data)
          headers core/attr-names]
      (spit out-file (str (s/join "\t" headers) "\n"))
      (->> data
           (pmap calculate-features)
           (map format-features)
           (s/join "\n")
           (spit out-file)))))
