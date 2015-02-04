(ns pageclass.train
  (:require [clojure.string :as s]
            [pageclass.core :as core])
  (:import [java.util Random]
           [weka.core Instances DenseInstance SerializationHelper]
           [weka.classifiers Evaluation]
           [weka.classifiers.evaluation.output.prediction PlainText]
           [weka.classifiers.trees RandomForest]))

(defn- create-classifier
  "Creates new classifier instance.
  Also try other subclasses of: http://weka.sourceforge.net/doc.dev/weka/classifiers/AbstractClassifier.html"
  []
  (RandomForest.))

(defn- load-data [filename]
  "Reads tab separated training data from file.
  Returns without header line"
  (let [lines (-> filename slurp s/split-lines)]
    (rest (map #(s/split % #"\t") lines))))

(defn- prepare-instances [filename]
  "Create Instances with training data from file"
  (let [dataset (Instances. "data" core/instance-attributes 0)
        data (load-data filename)
        attr-count (count core/attr-names)]
    (doseq [line data]
      (let [instance (doto (DenseInstance. attr-count)
                       (.setDataset dataset)
                       (.setValue 0 (first line)))]
        (loop [v (rest line) idx 1]
          (when (seq v)
            (.setValue instance idx (Double/parseDouble (first v)))
            (recur (rest v) (inc idx))))
        (.add dataset instance)))
    (.setClassIndex dataset 0)
    dataset))

(defn train
  "Trains classifier using data from provided file.
  Serializes to pageclass.model"
  [data-file]
  (let [classifier (create-classifier)]
    (.buildClassifier classifier (prepare-instances data-file))
    (SerializationHelper/write "resources/pageclass.model" classifier)))

(defn cross-validate
  "Does 10-fold cross-validation on data from provided file.
  Prints details to console. No model is created"
  [data-file]
  (let [instances (prepare-instances data-file)
        evaluator (Evaluation. instances)
        folds 10
        output (doto (PlainText.)
                 (.setBuffer (StringBuffer.))
                 (.setOutputDistribution true)
                 (.setAttributes (format "1-%s" (count core/attr-names))))]
    (.crossValidateModel evaluator (create-classifier) instances folds (Random. 1) (to-array [output]))
    (println (.toSummaryString evaluator))
    (println (.toClassDetailsString evaluator))
    (println (.toMatrixString evaluator))))

(defn evaluate
  "Evaluates serialized trained classifier on data from provided file.
  Prints details to console"
  [data-file]
  (let [classifier (SerializationHelper/read "resources/pageclass.model")
        instances (prepare-instances data-file)
        evaluator (Evaluation. instances)]
    (.evaluateModel evaluator classifier instances (to-array []))
    (println (.toSummaryString evaluator))
    (println (.toMatrixString evaluator))))