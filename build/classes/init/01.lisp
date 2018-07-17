;; General-purpose functions contributed by Jean-Pierre Gaillardin
;; May 2005.

(setq *package* (find-package "SYSTEM"))

;; ------------------------------------------------
;; GENSYM
;; ------------------------------------------------
(defparameter *gensym-counter* 1800)

(defun gensym nil
 (defparameter *gensym-counter* (+ 1 *gensym-counter*))
 (intern (concatenate 'STRING "#:G" *gensym-counter*)))

;; ------------------------------------------------
;; ENDP     (simple version)
;; ------------------------------------------------
(defun endp (l)
  (null l)
  )

;; ------------------------------------------------
;; ERROR    (very simple version)
;; ------------------------------------------------
; should be a primitive and throw a Java Exception

(defun error (&rest l)
  (print "**** ERROR ****")
  (print l)
  )
  
(export '(error gensym endp))
;; todo: These should all eventually move into the main code.
