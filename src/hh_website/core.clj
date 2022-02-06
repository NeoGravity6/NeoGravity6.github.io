(ns hh-website.core
  (:require [stasis.core :as stasis]))

(def pages {"/index.html" "<h1>Welcome!</h1>"})
(def target-dir "docs")

(def app (stasis/serve-pages pages))

(defn write-cname [out]
  (spit (str out "/CNAME") "henryhosono.me"))

(defn export []
  (stasis/empty-directory! target-dir)
  (stasis/export-pages pages target-dir)
  (write-cname target-dir)
  (println "Build COMPLETE!"))
