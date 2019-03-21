(ns session
  (:require [coast]
            [buddy.hashers :as hashers]
            [components :refer [container tc link-to table thead tbody td th tr button-to text-muted mr2 dl dd dt submit input label submit-block]]))


(defn build [request]
  (container {:mw 6}
    (when-let [error (get request :error/message)]
     [:div {:class "bg-washed-red red white pa3 mb4 br1 w-100"}
       error])

    [:h1 {:class "f1-l f-subheadline-l tc"} "Welcome back"]

    (coast/form-for ::create
      (label {:for "member/email"} "Email")
      (input {:type "email" :name "member/email" :value (-> request :params :member/email)})

      [:div.mb3]

      (label {:for "member/password"} "Password")
      (input {:type "password" :name "member/password" :value (-> request :params :member/password)})

      [:div.mb3]
      (submit-block "Sign in"))))


(defn create [request]
  (let [email (get-in request [:params :member/email])
        member (coast/find-by :member {:email email})
        [valid? errors] (-> (get request :params)
                            (select-keys [:member/email :member/password])
                            (coast/validate [[:email [:member/email]]
                                             [:required [:member/password]]])
                            (get :member/password)
                            (hashers/check (get member :member/password))
                            (coast/rescue))]
    (if (and (nil? errors)
             (true? valid?))
      (-> (coast/redirect-to :home/dashboard)
          (assoc :session {:member/email email})
          (assoc :flash "flash message"))
      (build (merge request errors {:error/message "Invalid email or password"})))))


(defn delete [request]
  (-> (coast/redirect-to :home/index)
      (assoc :session nil)))
