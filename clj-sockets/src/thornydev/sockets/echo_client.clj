(ns thornydev.sockets.echo-client
  (:import (java.io BufferedReader IOException InputStreamReader PrintWriter)
           (java.net Socket InetAddress)))

(def port 8000)

(defn get-user-input []
  (print "Msg for the server: ")
  (flush)
  (read-line))

(defn echo-client []
  (with-open [echo-sock (Socket. (InetAddress/getLocalHost) port)
              client-out (PrintWriter. (.getOutputStream echo-sock) true)
              client-in (BufferedReader.
                         (InputStreamReader.
                          (.getInputStream echo-sock)))]

    ;; read input from user on stdin
    (loop [input (get-user-input)]
      (when-not (nil? input)
        (.println client-out input)
        (flush)
        (println (.readLine client-in))
        (when-not (= ":quit" input)
          (recur (get-user-input))))))

  (println "Client closed down"))
