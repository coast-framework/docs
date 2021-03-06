(ns home
  (:require [coast]
            [components :refer [container hero table thead td tr tbody th link-to button-to]]
            [helpers]))


(defn index [request]
  [:div
   (hero
    [:div {:class "mw8 center"}
     [:div {:class "cf"}
      [:div {:class "fl w-50-ns w-100 near-black ph3"}
       [:h1 {:class "f2 f1-l fw3 lh-title"} "Easy clojure web development"]
       [:p {:class "lh-copy measure"}
        "Coast is a complete web application framework for developing web apps in less time and more fun"]
       [:a {:class "dn dib-ns no-underline br2 shadow-4 grow v-mid bg-green white ba b--green ph4 pv3 mb3 br1 mt3" :href (coast/url-for ::docs)} "Get Started with Coast"]]
      [:div {:class "fl w-50-ns w-100 ph3 mt2"}
       (coast/raw "<pre><code style=\"padding: 20px\" class=\"clojure\">(ns server
  (:require [coast]))

(defn home [request]
  (coast/ok \"You're coasting on clojure\"))

(def routes (coast/routes [:get \"/\" ::home]))

(def app (coast/app {:routes routes}))

(coast/server app {:port 1337})</code></pre>")

       [:a {:class "dn-ns dib no-underline br2 shadow-4 grow v-mid bg-green white ba b--green ph4 pv3 mb3 br1 mt3" :href (coast/url-for ::docs)} "Get Started with Coast"]]]])

   [:div {:class "pv6 hero-topo-bg white"}
    [:div {:class "tc-l ph3"}
     [:h2 {:class "f2 f1-l fw3 mb0 lh-title mb4"}
      "The missing clojure web framework"]
     [:p {:class "lh-copy measure center-l"}
      "Making a modern web app can be crazy. Between tracking down up-to-date, secure dependencies and your own application code, things can get out of control quickly.
       Coast on Clojure makes it much easier and more fun.
       It includes everything you need to make great web applications."]]]

   (hero
    [:div {:class "cf mw8 center near-black"}
     [:div {:class "fl w-100 w-third-ns ph4"}
      [:h3 {:class "f3 f2-l fw3 mb0 lh-title"}
        "⚡️ Fast"]
      [:p {:class "lh-copy measure"}
        "Coast was made to take your ideas from thought to completion to as fast as possible"]]

     [:div {:class "fl w-100 w-third-ns ph4"}
      [:h3 {:class "f3 f2-l fw3 mb0 lh-title"}
        "👮‍♂️ Secure"]
      [:p {:class "lh-copy measure"}
        "Coast takes security seriously and helps you avoid many common security mistakes"]]

     [:div {:class "fl w-100 w-third-ns ph4"}
      [:h3 {:class "f3 f2-l fw3 mb0 lh-title"}
        "🎉 Fun"]
      [:p {:class "lh-copy measure"}
        "Make web development fun again with consistent syntax and lightning fast feedback via the REPL"]]])])


(defn doc [request]
  (let [filename (get-in request [:params :doc])]
    [:div {:class "grid bg-nearest-white"}
     [:div {:class "pa4 bg-nearest-white sidebar-container"}
      [:div {:class "fr-l sidebar"}
       (coast/raw
         (slurp "html/readme.md"))]]
     [:div {:class "ph4 bg-white content"}
      (coast/raw
        (slurp (str "html/" filename ".md")))]]))


(defn docs [request]
  (doc {:params {:doc "installation"}}))


(defn screencast [request]
  [:div "screencast!"])


(defn dashboard [request]
  (let [members (coast/q '[:select * :from member])
        invites (coast/q '[:select * :from invite])
        posts (coast/q '[:select * :from post])]
    (container {:mw 9}
      [:h1 {:class "f2"} "Members"]
      (table
       (thead
         (tr
           (th "id")
           (th "email")
           (th "first-name")
           (th "last-name")
           (th "updated-at")
           (th "created-at")
           (th)
           (th)))
       (tbody
         (for [member members]
           (tr
             (td (:member/id member))
             (td (:member/email member))
             (td (:member/first-name member))
             (td (:member/last-name member))
             (td (:member/updated-at member))
             (td (:member/created-at member))
             (td
               (link-to (coast/url-for :member/edit member) "Edit"))
             (td
               (button-to (coast/action-for :member/delete member) {:data-confirm "Are you sure?"} "Delete"))))))

      [:h1 {:class "f2"} "Invites"]
      (table
       (thead
         (tr
           (th "id")
           (th "email")
           (th "approved-at")
           (th "updated-at")
           (th "created-at")
           (th "code")
           (th)
           (th)))
       (tbody
         (for [invite invites]
           (tr
             (td (:invite/id invite))
             (td (:invite/email invite))
             (td (:invite/approved-at invite))
             (td (:invite/updated-at invite))
             (td (:invite/created-at invite))
             (td (:invite/code invite))
             (td
               (button-to (coast/action-for :invite/approve invite) "Approve"))
             (td
               (link-to (coast/url-for :invite/edit invite) "Edit"))
             (td
               (button-to (coast/action-for :invite/delete invite) {:data-confirm "Are you sure?"} "Delete"))))))

      [:h1 {:class "f2"} "Posts"]
      (when (not (empty? posts))
       (link-to (coast/url-for :post/build) "New post"))

      (when (empty? posts)
       [:div.tc
         (link-to (coast/url-for :post/build) "New post")])
      (table
       (thead
         (tr
           (th "id")
           (th "member")
           (th "title")
           (th "slug")
           (th "body")
           (th "published-at")
           (th "updated-at")
           (th "created-at")))
       (tbody
         (for [post posts]
           (tr
             (td (:post/id post))
             (td (:post/member post))
             (td (:post/title post))
             (td (:post/slug post))
             (td (helpers/ellipsis (:post/body post) 20))
             (td (:post/published-at post))
             (td (:post/updated-at post))
             (td (:post/created-at post))
             (td
               (link-to (coast/url-for :post/view post) "View"))
             (td
               (link-to (coast/url-for :post/edit post) "Edit"))
             (td
               (button-to (coast/action-for :post/delete post) {:data-confirm "Are you sure?"} "Delete")))))))))


(defn not-found [request]
  [:html
   [:head
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    (coast/css "bundle.css")
    (coast/js "bundle.js")]
   [:body
    [:h1 "Couldn't find what you were looking for"]]])


(defn server-error [request]
  (when (some? (:exception request))
    (println (:exception request)))
  [:html
    [:head
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
     (coast/css "bundle.css")
     (coast/js "bundle.js")]
    [:body
     [:h1 "Something went wrong!"]]])
