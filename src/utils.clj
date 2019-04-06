(ns utils
  (:import (javax.crypto Mac)
           (javax.crypto.spec SecretKeySpec)
           (org.apache.commons.codec.binary Hex)))

(defn hmac [secret data]
  (let [algo "HmacSHA1"
        signing-key (SecretKeySpec. (.getBytes secret) algo)
        mac (doto (Mac/getInstance algo) (.init signing-key))]
    (str "sha1="
         (Hex/encodeHexString (.doFinal mac (.getBytes data))))))
