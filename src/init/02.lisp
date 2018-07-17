;; These macros were contributed by Jean-Pierre Gaillardin
;; May 2005.

(setq *package* (find-package "SYSTEM"))

;; --- Functions needed to implement macros -----

(defun combine-exprs (lft rgt expr)
  (if (and (constantp lft) (constantp rgt)) (list 'quote expr)
     (if (null rgt) (list 'list lft)
       (if (and (consp rgt) (eql (car rgt) 'list))  (cons 'list (cons lft (cdr rgt )))
         (list 'cons lft rgt ))))) 


(defun f-backquote (expr)
  (if (null expr) nil 
    (if (atom expr) (list 'quote expr)
       (if (eq (car expr) :COMMA) (second expr)  
         (if (and (consp (car expr)) (eq (car (car expr)) :COMMA-ATSIGN )) 
            (list 'append (second (car expr)) (f-backquote (cdr expr))) 
         (combine-exprs (f-backquote (car expr)) 
            (f-backquote (cdr expr)) expr) )))))

(defmacro backquote (expr)
   (f-backquote expr))

;; ------------------------------------------------
;; COND
;; ------------------------------------------------
(defun f-cond (l)
  (if (null l) nil
    (let ((clause (car l))) 
      `(if ,(car clause) (progn ,@(cdr clause))
        ,(f-cond (cdr l))))))

(defmacro cond (&rest l) 
   (f-cond l))

;; (OB) 13 May 2005, added the first parameter to make code slightly more readable.
;; ------------------------------------------------
;; PROG1
;; ------------------------------------------------
(defmacro prog1 (first &rest l)
"(prog1 first {rest}*)
This macro is like progn, but returns the value of it's first form.
Note that prog1 always returns a single value, even though the form
may have evaluated to multiple values."
  (let ((x (gensym)))
   `(let ((,x ,first)) ,@l ,x)))

;; Added (OB) 13 May 2005
;; ------------------------------------------------
;; PROG2
;; ------------------------------------------------
(defmacro prog2 (first second &rest l)
"(prog2 first second {rest}*)
This macro is like progn, but returns the value of it's second form.
Note that prog2 always returns a single value, even though the form
may have evaluated to multiple values."
  (let ((x (gensym)))
   `(progn ,first (let ((,x ,second)) ,@l ,x))))

;; Removed (mh) 11 May 2005.  This is the same as the builtin POP function.
;; ------------------------------------------------
;; NETTL
;; ------------------------------------------------
;; not Common Lisp, from Le_Lisp but this function is cool
;;(defmacro nettl (l)
;;  `(prog1 (car ,l) (setq ,l (cdr ,l))))


(export '(backquote cond prog1 prog2))