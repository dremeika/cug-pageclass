(ns pageclass.features
  (:require [clojure.tools.logging :as log]
            [net.cgrand.enlive-html :as html])
  (:import [de.l3s.boilerpipe.extractors KeepEverythingExtractor]))

(def everything-extractor (KeepEverythingExtractor/INSTANCE))

(defn- html-element
  "Count html tags in html"
  [path]
  (partial
    (fn [path page] (count (html/select (:html page) path)))
    path))

(defn- text-size
  "Size of text extracted by given Boilerpipe extractor."
  [extractor]
  (partial
    (fn [extractor page]
      (count (.getText extractor (:text page))))
    extractor))

(defn- normalize-url
  "Removes trailing slashes "
  [url]
  (if (and url (.endsWith url "/"))
    (recur (.substring url 0 (- (count url) 1)))
    url))

(def feature-generators
  [
   [:htmlimg    (html-element [:img])] ;Image count
   [:txtall     (text-size everything-extractor)] ;Size of all text
   [:urllen     (fn [p] (count (:url p)))] ;Url length
   ])

(def feature-names (map #(-> % first name) feature-generators))

(defn generate-features
  "Generate features for given url and html"
  [url html]
  (try
    (let [page {:text html
                :html (html/html-snippet html)
                :url (normalize-url url)}]
      (reduce
        (fn [res [_ func]]
          (conj res (func page)))
        []
        feature-generators))
    (catch Exception e
      (log/errorf e "Failed with features extraction for '%s'" url)
      (throw e))))
