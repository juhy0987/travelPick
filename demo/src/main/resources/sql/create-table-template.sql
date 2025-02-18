CREATE TABLE user(  
  email VARCHAR(255) NOT NULL COMMENT 'Email',
  pass_hash VARCHAR(255) NOT NULL COMMENT 'Password Hash',
  name VARCHAR(255) NOT NULL COMMENT 'Name',
  refresh_token VARCHAR(255) COMMENT 'Refresh Token',
  created TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Created',
  PRIMARY KEY (email)
) COMMENT 'User';

CREATE TABLE resort(
  id CHAR(36) NOT NULL DEFAULT(UUID()) COMMENT 'ID',
  parent_id CHAR(36) NOT NULL COMMENT 'Location ID',
  name VARCHAR(255) NOT NULL COMMENT 'Name',
  alias VARCHAR(255) COMMENT 'Alias',
  importance_rank INT NOT NULL COMMENT 'Importance Rank',
  description TEXT COMMENT 'Description',
  PRIMARY KEY (id),
  FOREIGN KEY (parent_id) REFERENCES resort(id)
) COMMENT 'Resort';

CREATE TABLE location(
  id CHAR(36) NOT NULL DEFAULT(UUID()) COMMENT 'ID',
  parent_id CHAR(36) COMMENT 'Parent ID',
  resort_id CHAR(36) COMMENT 'Resort ID',
  name VARCHAR(255) NOT NULL COMMENT 'Name',
  alias VARCHAR(255) COMMENT 'Alias',
  coordinates POINT NOT NULL COMMENT 'Coordinates',
  timezone VARCHAR(255) COMMENT 'Timezone',
  PRIMARY KEY (id),
  SPATIAL INDEX (coordinates),
  FOREIGN KEY (parent_id) REFERENCES location(id),
  FOREIGN KEY (resort_id) REFERENCES resort(id)
) COMMENT 'Location';

CREATE TABLE review(
  id CHAR(36) NOT NULL DEFAULT(UUID()) COMMENT 'ID',
  location_id CHAR(36) NOT NULL COMMENT 'Location ID',
  user_id CHAR(36) NOT NULL COMMENT 'User ID',
  content TEXT NOT NULL COMMENT 'Content',
  created TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Created',
  updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated',
  PRIMARY KEY (id),
  FOREIGN KEY (location_id) REFERENCES location(id),
  FOREIGN KEY (user_id) REFERENCES user(email)
) COMMENT 'Review';

CREATE TABLE photo(
  id CHAR(36) NOT NULL DEFAULT(UUID()) COMMENT 'ID',
  location_id CHAR(36) COMMENT 'Location ID',
  review_id CHAR(36) COMMENT 'Review ID',
  data BLOB NOT NULL COMMENT 'Data',
  PRIMARY KEY (id),
  FOREIGN KEY (location_id) REFERENCES location(id),
  FOREIGN KEY (review_id) REFERENCES review(id)
) COMMENT 'Photo';
