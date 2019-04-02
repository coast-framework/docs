(ns helpers)

(defn ellipsis [s n]
  (when (and (string? s)
             (number? n))
    (let [end (if (> (count s) n)
                n
                (count s))]
      (str (subs s 0 end)
           "..."))))
