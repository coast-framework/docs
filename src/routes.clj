(ns routes
  (:require [coast]
            [components]
            [middleware]))

(def routes
  (coast/routes

    (coast/site
      (coast/with middleware/title
       (coast/with-layout :components/doc-layout
         [:get "/docs" :home/docs]
         [:get "/docs/:doc.md" :home/doc])

       (coast/with-layout :components/layout
         [:get "/" :home/index]
         [:get "/screencast" :home/screencast]
         [:get "/sign-up" :member/build]
         [:post "/members" :member/create]
         [:get "/sign-in" :session/build]
         [:post "/sessions" :session/create]

         [:resource :invite :only [:build :create]]

         (coast/with middleware/auth middleware/current-member
           [:get "/dashboard" :home/dashboard]
           [:delete "/sessions" :session/delete]
           [:resource :member :except [:index :view :build :create]]
           [:resource :invite :except [:index :view :build :create]]
           [:resource :post :only [:build :create :edit :change :delete]]
           [:post "/posts/preview" :post/preview]
           [:put "/invite/:invite-id/approve" :invite/approve])

         [:get "/posts/:post-slug" :post/view]
         [:get "/posts" :post/index])))


    (coast/api
      (coast/with-prefix "/api"
        [:get "/" :api.home/index]
        [:post "/releases" :api.home/release]))))
