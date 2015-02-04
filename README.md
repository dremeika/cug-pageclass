# Page Function Classifier for Vilnius Clojure Dojo

Code ready for modification to increase page classifier accuracy and precision.

## Usage

Unzip labeled data


    unzip dataset.zip

Generate page features


    lein generate-features features.tsv

Cross-validate classifier


    lein cross-validate features.tsv

Train classifier


    lein train features.tsv

Evaluate classifier (optional, do not use data used for training)


    lein evaluate test.tsv

Test classifier with real page


    lein classify "http://..."

Add page to dataset (optional)


    lein add-page "L" "http://..."


## Used Tools

* Feature generation: [Enlive](https://github.com/cgrand/enlive), [Boilerpipe](https://code.google.com/p/boilerpipe/)
* Machine learning: [Weka](http://www.cs.waikato.ac.nz/ml/weka/)

## Main points of modification

* Use different classifier: `src/pageclass/train.clj` Available ones extend [AbstractClassifier](http://weka.sourceforge.net/doc.dev/weka/classifiers/AbstractClassifier.html).
* Generate meaningful features: `src/pageclass/features.clj`

## Page labels

* A - Article
* D - Discussion, Forum
* F - Form
* H - Home page
* L - Listing
* I - Single item or product page in e-shop
* M - Media
* Z - Contacts page
* X - Unknown