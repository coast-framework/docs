(ns routes
  (:require [coast]))

(def routes
  (coast/routes
    (coast/site-routes :components/layout
      [:get "/" :home/index]
      [:get "/docs" :home/docs]
      [:get "/docs/:doc.md" :home/doc]
      [:get "/screencast" :home/screencast]

      [:get "/sign-up" :member/build]
      [:post "/members" :member/create]
      [:get "/sign-in" :session/build]
      [:post "/sessions" :session/create]

      [:resource :invite :only [:build :create]]

      (coast/wrap-routes :middleware/auth
        [:get "/dashboard" :home/dashboard]
        [:delete "/sessions" :session/delete]
        [:resource :member :except [:index :view :build :create]]
        [:resource :invite :except [:index :view :build :create]]
        [:put "/invite/:invite-id/approve" :invite/approve])

      [:404 :home/not-found]
      [:500 :home/server-error])))
