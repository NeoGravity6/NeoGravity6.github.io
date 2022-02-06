(ns hh-website.core
  (:require
    [clojure.string :as cstr]
    [stasis.core :as stasis]
    [markdown.core :as md]
    [hiccup.page :as hiccup]))

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
     [:link {:type "text/css" :href "/css/style.css" :rel "stylesheet"}]
     [:body
      [:div {:class "header"}
       [:div {:class "name"}
        [:a {:href "/"} "Home page"]
        [:div {:class "header-right"}
         [:a {:href "/posts"} "Posts"]]]]
      page]
     [:footer
      [:p "This is the footer"]]]))

(defn format-pages [m]
  (let [html-keys (keys m)
        page-data (map apply-header-footer (vals m))]
    (zipmap html-keys page-data)))

(defn merge-website-assets! [root-dir]
  (let [page-map (format-pages (read-and-convert! root-dir))
        css-map  (stasis/slurp-directory root-dir #".*\.css$")]
    (stasis/merge-page-sources {:css   css-map
                                :pages page-map})))

(def app (stasis/serve-pages (merge-website-assets! source-dir)))

(defn write-cname [out]
  (spit (str out "/CNAME") "henryhosono.me"))

(defn- load-export-dir []
  (stasis/slurp-directory target-dir #"\.[^.]+$"))

(defn export []
  (let [old-dir (load-export-dir)]
    (stasis/empty-directory! target-dir)
    (stasis/export-pages (merge-website-assets! source-dir) target-dir)
    (write-cname target-dir)
    (println "Build COMPLETE!")
    (stasis/report-differences old-dir (load-export-dir))))
