(ns hh-website.core
  (:require
    [clojure.string :as cstr]
    [stasis.core :as stasis]
    [markdown.core :as md]
    [hiccup.page :as hiccup]
    [optimus.prime :as optimus]
    [optimus.assets :as assets]
    [optimus.optimizations :as optimizations]
    [optimus.strategies :refer [serve-live-assets]]
    [optimus.export]
    [ring.middleware.content-type :refer [wrap-content-type]]
    [hh-website.components.sidebar :refer [sidebar]]
    [hh-website.views.home :refer [home]]))

(def target-dir "docs")
(def source-dir "resources")

(def css-assets ["/css/base.css"
                 "/css/home.css"])

(def img-assets ["/img/HH_logo.png"
                 "/img/HH-photo.png"
                 "/img/linkedin.png"
                 "/img/favicon.ico"
                 "/img/apple-touch-icon.png"])

(def font-assets ["/fonts/Catamaran-Bold.ttf"
                  "/fonts/Catamaran-Regular.ttf"])

(def pages {"/index.html" home})

(defn read-and-convert! [src]
  (let [data         (stasis/slurp-directory src #".*\.md$")
        html-paths   (map #(cstr/replace % #".md" ".html") (keys data))
        html-content (map md/md-to-html-string (vals data))]
    (zipmap html-paths html-content)))

(defn apply-header-footer [page]
  (hiccup/html5
    {:lang "en"}
    [:head
     [:title "Henry Hosono"]
     [:meta {:charset "utf-8"}]
     [:meta {:name    "viewport"
             :content "width=device-width, initial-scale=1.0"}]
     [:link {:type "text/css" :href "/css/base.css" :rel "stylesheet"}]
     [:link {:rel "icon" :href "/img/favicon.ico"}]
     [:link {:rel "apple-touch-icon" :href "/img/apple-touch-icon.png"}]
     [:body
      sidebar
      [:div {:class "content"}
       page]]]))

(defn get-pages [m]
  (let [html-keys (keys m)
        page-data (map apply-header-footer (vals m))]
    (zipmap html-keys page-data)))

(defn get-assets []
  (let [public-dir "public"]
    (concat
      (assets/load-bundle public-dir "styles.css" css-assets)
      (assets/load-assets public-dir (concat img-assets font-assets)))))

(def app
  (-> (stasis/serve-pages (get-pages (merge pages (read-and-convert! source-dir))))
      (optimus/wrap get-assets optimizations/all serve-live-assets)
      wrap-content-type))

(defn write-cname [out]
  (spit (str out "/CNAME") "henryhosono.me"))

(defn- load-export-dir []
  (stasis/slurp-directory target-dir #"\.[^.]+$"))

(defn export []
  (let [old-dir (load-export-dir)
        assets  (optimizations/all (get-assets) {})
        pages   (get-pages (merge pages (read-and-convert! source-dir)))]
    (stasis/empty-directory! target-dir)
    (optimus.export/save-assets assets target-dir)
    (stasis/export-pages pages target-dir {:optimus-assets assets})
    (write-cname target-dir)
    (println "Build COMPLETE!")
    (stasis/report-differences old-dir (load-export-dir))))
