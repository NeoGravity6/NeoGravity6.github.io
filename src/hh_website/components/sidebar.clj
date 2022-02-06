(ns hh-website.components.sidebar)

(def sidebar
  [:div {:class "sidebar"}
   [:div {:class "sidebar-header"}
    [:img {:class "sidebar-logo"
           :src   "/img/HH_logo.png"
           :alt   "HH Logo"}]]
   [:span {:class "sidebar-separator"}]
   [:nav {:class "sidebar-menu"}
    [:ul
     [:li {:class "sidebar-menu-link"}
      [:a {:href "/"}
       "Home"]]]]])
