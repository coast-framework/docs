(ns migrations.20190318123355-create-table-member
  (:require [coast.db.migrations :refer :all]))

(defn change []
  (create-table :member
    (text :first-name)
    (text :last-name)
    (text :email :null false :unique true)
    (text :password :null false)
    (timestamps)))
