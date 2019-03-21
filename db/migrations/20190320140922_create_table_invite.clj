(ns migrations.20190320140922-create-table-invite
  (:require [coast.db.migrations :refer :all]))

(defn change []
  (create-table :invite
    (text :email :null false)
    (text :code :null false)
    (integer :approved-at)
    (timestamps)))
