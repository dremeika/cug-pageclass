(ns pageclass.core

  (:gen-class
  :name "cug.pageclass.PageClassifier"
  :prefix "-"
  :main false
  :methods [[classify [String String] String]])

  (:require [clojure.java.io :as io]
            [pageclass.features :as feat])
  (:import [weka.core Attribute Instances DenseInstance SerializationHelper]
           [java.util ArrayList]))

(def attr-names (cons "class" feat/feature-names))
(def classes ["A" "D" "F" "H" "L" "I" "M" "Z" "X"])

(def classfier (delay (-> "pageclass.model"
                          io/resource
                          io/input-stream
                          SerializationHelper/read)))

(defn create-attributes
  "Creates Weka Attributes for class and features"
  []
  (let [attrs (ArrayList.)] ;Weka expects instance of ArrayList
    (.add attrs (Attribute. (first attr-names) classes))
    (doseq [a (rest attr-names)]
      (.add attrs (Attribute. a)))
    attrs))

(def instance-attributes (create-attributes))

(defn- weka-attributes
  "Creates data instance and populates with features"
  [features]
  (let [dataset (Instances. "classify" instance-attributes 1)
        instance (DenseInstance. (count instance-attributes))]
    (.setClassIndex dataset 0)
    (doto instance
      (.setDataset dataset)
      (.setClassMissing))
    (loop [fts features idx 1]
      (when (seq fts)
        (.setValue instance idx (double (first fts)))
        (recur (rest fts) (inc idx))))
    [dataset instance]))

(defn- probabilities
  "Returns probabilities of all class memberships for a given instance"
  [instance]
  (map
    #(vector %2 %1)
    (vec (.distributionForInstance @classfier instance)) classes))

(defn classify
  [url html & {:keys [probs] :or {probs false}}]
  (let [features (feat/generate-features url html)
        [dataset instance] (weka-attributes features)
        class-index (.classifyInstance @classfier instance)]

      {:class (-> dataset (.classAttribute) (.value class-index))
       :probabilities (when probs (probabilities instance))}))

(defn -classify
  "Java method. Returns page class"
  [_ url html]
  (:class (classify url html)))