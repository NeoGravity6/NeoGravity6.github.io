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
    [ring.middleware.content-type :refer [wrap-content-type]]))

(def target-dir "docs")
(def source-dir "resources")

(defn read-and-convert! [src]
  (let [data         (stasis/slurp-directory src #".*\.md$")
        html-paths   (map #(cstr/replace % #".md" ".html") (keys data))
        html-content (map md/md-to-html-string (vals data))]
    (zipmap html-paths html-content)))

(defn apply-header-footer [page]
  (hiccup/html5
    {:lang "en"}
    [:head
     [:title "Static website!"]
     [:meta {:charset "utf-8"}]
     [:meta {:name    "viewport"
             :content "width=device-width, initial-scale=1.0"}]
     [:link {:type "text/css" :href "/css/base.css" :rel "stylesheet"}]
     [:body
      [:div {:class "header"}
       [:div {:class "name"}
        [:a {:href "/"} "Home page"]
        [:div {:class "header-right"}
         [:a {:href "/posts"} "Posts"]]]]
      page]
     [:footer
      [:p "This is the footer"]]]))

(defn get-pages [m]
  (let [html-keys (keys m)
        page-data (map apply-header-footer (vals m))]
    (zipmap html-keys page-data)))

(defn get-assets []
  (let [public-dir "public"]
    (concat
      (assets/load-bundle public-dir "styles.css" ["/css/base.css"])
      (assets/load-assets public-dir []))))

(def app
  (-> (stasis/serve-pages (get-pages (read-and-convert! source-dir)))
      (optimus/wrap get-assets optimizations/all serve-live-assets)
      wrap-content-type))

(defn write-cname [out]
  (spit (str out "/CNAME") "henryhosono.me"))

(defn- load-export-dir []
  (stasis/slurp-directory target-dir #"\.[^.]+$"))

(defn export []
  (let [old-dir (load-export-dir)
        assets  (optimizations/all (get-assets) {})
        pages   (get-pages (read-and-convert! source-dir))]
    (stasis/empty-directory! target-dir)
    (optimus.export/save-assets assets target-dir)
    (stasis/export-pages pages target-dir {:optimus-assets assets})
    (write-cname target-dir)
    (println "Build COMPLETE!")
    (stasis/report-differences old-dir (load-export-dir))))
