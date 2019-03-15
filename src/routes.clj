(ns routes
  (:require [coast]))

(def routes
  (coast/routes
    (coast/site-routes :components/layout
      [:get "/" :home/index]
      [:get "/docs" :home/docs]
      [:get "/docs/:doc.md" :home/doc]
      [:get "/screencast" :home/screencast]
      [:404 :home/not-found]
      [:500 :home/server-error])))
