(ns pageclass.features-test
  (:require [midje.sweet :refer :all]
            [pageclass.features :refer [generate-features]]))

(def test-pages
  [["http://www.delfi.lt/news/daily/lithuania/j-siksniutes-gedintys-zmones-kas-tai-padare-nenusipelno-gyventi.d?id=66470672" "test/data/page1.html"]])

(facts "about feature generation"
  (let [[url filename] (first test-pages)
        text (slurp filename)
        features (generate-features url text)]
    features => [15 6703 118]))