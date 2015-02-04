(ns pageclass.http
  (require [clj-http.client :as client]))

(def conn-timeout 1000)

(def headers
  {"User-Agent"  "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.66 Safari/537.36"
   "Accept"      "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"})

(defn fetch [url]
  (client/get url {:headers headers
                   :conn-timeout conn-timeout}))