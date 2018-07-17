;; Simple general functions from CLTL2
;; Contributed by Ola Bini
;; May 2005

(setq *package* (find-package "SYSTEM"))

;; This is a dummy implementation, since we don't need the real thing right now. It just expands the body into an progn right now.
(defmacro eval-when (situation &rest body)
  `(progn ,@body))

(defmacro in-package (name)
  `(eval-when (:compile-toplevel :load-toplevel :execute) (setq *package* (find-package ,name))))

(in-package "SYSTEM")

(defun caar (l)
  (car (car l)))

(defun cadr (l)
  (second l))

(defun cdar (l)
  (cdr (car l)))

(defun cddr (l)
  (cdr (cdr l)))

(defun caaar (l)
  (car (car (car l))))

(defun caadr (l)
  (car (car (cdr l))))

(defun cadar (l)
  (car (cdr (car l))))

(defun caddr (l)
  (third l))

(defun cdaar (l)
  (cdr (car (car l))))

(defun cdadr (l)
  (cdr (car (cdr l))))

(defun cddar (l)
  (cdr (cdr (car l))))

(defun cdddr (l)
  (cdr (cdr (cdr l))))

(defun caaaar (l)
  (car (car (car (car l)))))

(defun caaadr (l)
  (car (car (car (cdr l)))))

(defun caadar (l)
  (car (car (cdr (car l)))))

(defun caaddr (l)
  (car (car (cdr (cdr l)))))

(defun cadaar (l)
  (car (cdr (car (car l)))))

(defun cadadr (l)
  (car (cdr (car (cdr l)))))

(defun caddar (l)
  (car (cdr (cdr (car l)))))

(defun cadddr (l)
  (fourth l))

(defun cdaaar (l)
  (cdr (car (car (car l)))))

(defun cdaadr (l)
  (cdr (car (car (cdr l)))))

(defun cdadar (l)
  (cdr (car (cdr (car l)))))

(defun cdaddr (l)
  (cdr (car (cdr (cdr l)))))

(defun cddaar (l)
  (cdr (cdr (car (car l)))))

(defun cddadr (l)
  (cdr (cdr (car (cdr l)))))

(defun cdddar (l)
  (cdr (cdr (cdr (car l)))))

(defun cddddr (l)
  (cdr (cdr (cdr (cdr l)))))

(defmacro unless (test &rest body)
  `(and (not ,test) (progn ,@body)))

(defmacro when (test &rest body)
  `(and ,test (progn ,@body)))

(defmacro let* (vars &rest body)
  (if (endp vars)
      `(progn ,@body)
      `(let (,(car vars)) (let* ,(cdr vars) ,@body))))

(defmacro return (value)
  `(return-from nil ,value))

(defmacro loop (&rest body)
  (let ((tagSym (gensym)))
    `(block nil
      (tagbody
         ,tagSym
         ,@body
         (go ,tagSym)))))

(defmacro dotimes (init &rest body)
  (let ((var (first init))
        (uptoForm (second init))
        (resultForm (third init))
        (uptoSym (gensym)))
    `(let ((,uptoSym ,uptoForm)
           (,var 0))
      (loop
       (when (not (< ,var ,uptoSym))
         (return ,resultForm))
       ,@body
       (setq ,var (1+ ,var))))))

(defmacro dolist (init &rest body)
  (let ((var (first init))
        (listForm (second init))
        (resultForm (third init))
        (listSym (gensym)))
    `(let* ((,listSym ,listForm)
            (,var (first ,listSym)))
      (loop
       (when (endp ,var)
         (return ,resultForm))
       ,@body
       (setq ,listSym (rest ,listSym))
       (setq ,var (first ,listSym))))))       

(defun terpri ()
  (princ #\Newline))

(defmacro setf (place value)
  (if (listp place)
      `(,(let ((name (car place)))
              (cond
                ((eq name 'aref) 'setf-aref)
                ((eq name 'car) 'setf-car)
                ((eq name 'cdr) 'setf-cdr)
                ((eq name 'documentation) 'setf-documentation)
                ((eq name 'gethash) 'setf-gethash)
                ((eq name 'symbol-function) 'setf-symbol-function)
                ((eq name 'symbol-plist) 'setf-symbol-plist)
                ((eq name 'symbol-value) 'setf-symbol-value)))
         ,@(cdr place)
         ,value)
      `(set ',place ,value)))

(export '(caar cadr cdar cddr caaar caadr cadar caddr cdaar cdadr cddar cdddr caaaar caaadr caadar caaddr cadaar cadadr caddar cadddr cdaaar cdaadr cdadar cdaddr cddaar cddadr cdddar cddddr unless when let* in-package eval-when return loop dotimes dolist terpri setf))

