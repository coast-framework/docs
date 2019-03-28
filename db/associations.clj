(ns associations
  (:require [coast.db.associations :refer [table belongs-to has-many tables]]))

(defn associations []
  (tables
   (table :member
     (has-many :posts))

   (table :post
     (belongs-to :member))))
