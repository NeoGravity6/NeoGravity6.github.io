(ns hh-website.views.home)

(def home
  [:div
   [:link {:type "text/css" :href "/css/home.css" :rel "stylesheet"}]
   [:div {:class "home-header"}
    [:img {:src   "/img/HH-photo.png"
           :alt   "Henry Hosono"
           :class "hh-photo"}]
    [:h1 {:class "home-title"}
     "Henry </br> Hosono"]]
   [:div {:class "home-content"}
    [:p "Hello, and welcome to my personal website!"]
    [:p "It's currently under development and will be available in a short while."]
    [:p "In the meantime, you can follow me on"
     [:span
      [:a {:href "https://www.linkedin.com/in/henry-hosono/"}
       [:img {:src   "/img/linkedin.png"
              :alt   "LinkedIn"
              :class "linkedin-logo"}]]]]]])
