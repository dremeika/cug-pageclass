(ns pageclass.run
  (:require [pageclass.core :refer [classify]]
            [pageclass.http :as http]))

(defn fetch-and-classify
  "Fetch page, classify and print its class and probabilities"
  [url]
  (let [html (:body (http/fetch url))
        {:keys [class probabilities]} (classify url html :probs true)]

    (println (format "Predicted '%s'. Class probabilities: %s"
                     class
                     (vec probabilities)))))
