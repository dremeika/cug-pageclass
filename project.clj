(defproject cug-pageclass "0.1.0-SNAPSHOT"
  :description "CUG: Weka+Clojure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :aot [pageclass.core]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.slf4j/slf4j-log4j12 "1.7.7"]
                 [clj-http "1.0.1"]
                 [enlive "1.1.5"]
                 [com.syncthemall/boilerpipe "1.2.2"]
                 [nz.ac.waikato.cms.weka/weka-dev "3.7.12"]]
  :plugins [[lein-midje "3.1.3"]]

  :profiles {:dev {:dependencies [[midje "1.6.3"]]}}

  :aliases {"generate-features"   ["run" "-m" "pageclass.prepare/generate-features"]
            "add-page"            ["run" "-m" "pageclass.prepare/add-page"]
            "train"               ["run" "-m" "pageclass.train/train"]
            "classify"            ["run" "-m" "pageclass.run/fetch-and-classify"]
            "evaluate"            ["run" "-m" "pageclass.train/evaluate"]
            "cross-validate"      ["run" "-m" "pageclass.train/cross-validate"]}
)
