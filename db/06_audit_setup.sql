CREATE INDEX idx_audit_table_time ON audit_log(table_name, operation_time);
CREATE INDEX idx_audit_db_user ON audit_log(db_user);

CREATE TRIGGER audit_users_insert
AFTER INSERT ON users
FOR EACH ROW
INSERT INTO audit_log (
    table_name, row_id, operation_type,
    db_user,
    new_values
)
VALUES (
    'users', NEW.id, 'INSERT',
    USER(),
    JSON_OBJECT(
        'username', NEW.username,
        'email', NEW.email,
        'role', NEW.role,
        'is_verified', NEW.is_verified
    )
);


CREATE TRIGGER audit_users_update
AFTER UPDATE ON users
FOR EACH ROW
INSERT INTO audit_log (
    table_name, row_id, operation_type,
    db_user,
    old_values, new_values
)
VALUES (
    'users', NEW.id, 'UPDATE',
    USER(),
    JSON_OBJECT(
        'email', OLD.email,
        'role', OLD.role,
        'is_verified', OLD.is_verified
    ),
    JSON_OBJECT(
        'email', NEW.email,
        'role', NEW.role,
        'is_verified', NEW.is_verified
    )
);

CREATE TRIGGER audit_users_delete
BEFORE DELETE ON users
FOR EACH ROW
INSERT INTO audit_log (
    table_name, row_id, operation_type,
    db_user,
    old_values
)
VALUES (
    'users', OLD.id, 'DELETE',
    USER(),
    JSON_OBJECT(
        'username', OLD.username,
        'email', OLD.email,
        'role', OLD.role
    )
);


CREATE TRIGGER audit_movies_insert
AFTER INSERT ON movies
FOR EACH ROW
INSERT INTO audit_log (
    table_name, row_id, operation_type,
    db_user,
    new_values
)
VALUES (
    'movies', NEW.id, 'INSERT',
    USER(),
    JSON_OBJECT(
        'tmdb_id', NEW.tmdb_id,
        'title', NEW.title,
        'release_date', NEW.release_date
    )
);

CREATE TRIGGER audit_movies_update
AFTER UPDATE ON movies
FOR EACH ROW
INSERT INTO audit_log (
    table_name, row_id, operation_type,
    db_user,
    old_values, new_values
)
VALUES (
    'movies', NEW.id, 'UPDATE',
    USER(),
    JSON_OBJECT(
        'title', OLD.title,
        'overview', OLD.overview,
        'runtime', OLD.runtime
    ),
    JSON_OBJECT(
        'title', NEW.title,
        'overview', NEW.overview,
        'runtime', NEW.runtime
    )
);

CREATE TRIGGER audit_movies_delete
BEFORE DELETE ON movies
FOR EACH ROW
INSERT INTO audit_log (
    table_name, row_id, operation_type,
    db_user,
    old_values
)
VALUES (
    'movies', OLD.id, 'DELETE',
    USER(),
    JSON_OBJECT(
        'tmdb_id', OLD.tmdb_id,
        'title', OLD.title
    )
);

CREATE TRIGGER audit_reviews_insert
AFTER INSERT ON reviews
FOR EACH ROW
INSERT INTO audit_log (
    table_name, row_id, operation_type,
    db_user,
    new_values
)
VALUES (
    'reviews', NEW.id, 'INSERT',
    USER(),
    JSON_OBJECT(
        'user_id', NEW.user_id,
        'movie_id', NEW.movie_id,
        'rating', NEW.rating
    )
);

CREATE TRIGGER audit_reviews_update
AFTER UPDATE ON reviews
FOR EACH ROW
INSERT INTO audit_log (
    table_name, row_id, operation_type,
    db_user,
    old_values, new_values
)
VALUES (
    'reviews', NEW.id, 'UPDATE',
    USER(),
    JSON_OBJECT(
        'rating', OLD.rating,
        'review_text', OLD.review_text
    ),
    JSON_OBJECT(
        'rating', NEW.rating,
        'review_text', NEW.review_text
    )
);

CREATE TRIGGER audit_reviews_delete
BEFORE DELETE ON reviews
FOR EACH ROW
INSERT INTO audit_log (
    table_name, row_id, operation_type,
    db_user,
    old_values
)
VALUES (
    'reviews', OLD.id, 'DELETE',
    USER(),
    JSON_OBJECT(
        'user_id', OLD.user_id,
        'movie_id', OLD.movie_id,
        'rating', OLD.rating
    )
);

CREATE TRIGGER audit_comments_insert
AFTER INSERT ON comments
FOR EACH ROW
INSERT INTO audit_log (
    table_name, row_id, operation_type,
    db_user,
    new_values
)
VALUES (
    'comments', NEW.id, 'INSERT',
    USER(),
    JSON_OBJECT(
        'review_id', NEW.review_id,
        'user_id', NEW.user_id
    )
);

CREATE TRIGGER audit_comments_delete
BEFORE DELETE ON comments
FOR EACH ROW
INSERT INTO audit_log (
    table_name, row_id, operation_type,
    db_user,
    old_values
)
VALUES (
    'comments', OLD.id, 'DELETE',
    USER(),
    JSON_OBJECT(
        'review_id', OLD.review_id,
        'user_id', OLD.user_id,
        'comment_text', OLD.comment_text
    )
);

CREATE TRIGGER audit_collections_insert
AFTER INSERT ON collections
FOR EACH ROW
INSERT INTO audit_log (
    table_name, row_id, operation_type,
    db_user,
    new_values
)
VALUES (
    'collections', NEW.id, 'INSERT',
    USER(),
    JSON_OBJECT(
        'user_id', NEW.user_id,
        'name', NEW.name
    )
);

CREATE TRIGGER audit_collections_update
AFTER UPDATE ON collections
FOR EACH ROW
INSERT INTO audit_log (
    table_name, row_id, operation_type,
    db_user,
    old_values, new_values
)
VALUES (
    'collections', NEW.id, 'UPDATE',
    USER(),
    JSON_OBJECT(
        'name', OLD.name,
        'description', OLD.description
    ),
    JSON_OBJECT(
        'name', NEW.name,
        'description', NEW.description
    )
);

CREATE TRIGGER audit_collections_delete
BEFORE DELETE ON collections
FOR EACH ROW
INSERT INTO audit_log (
    table_name, row_id, operation_type,
    db_user,
    old_values
)
VALUES (
    'collections', OLD.id, 'DELETE',
    USER(),
    JSON_OBJECT(
        'user_id', OLD.user_id,
        'name', OLD.name
    )
);

