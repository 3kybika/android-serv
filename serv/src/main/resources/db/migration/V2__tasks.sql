CREATE TABLE IF NOT EXISTS  Tasks (
  global_id SERIAL NOT NULL PRIMARY KEY,
  author_id INTEGER  REFERENCES users(id),
  changed_by INTEGER  REFERENCES users(id),
  priority INTEGER,

  name VARCHAR(255) NOT NULL,
  about VARCHAR(255),

  deadline TIMESTAMP WITH TIME ZONE,
  notificationTime TIMESTAMP WITH TIME ZONE,
  lastChangeTime TIMESTAMP WITH TIME ZONE,

  completed BOOLEAN,
  deleted BOOLEAN
);
