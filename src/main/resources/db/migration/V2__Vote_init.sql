CREATE TABLE vote (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    answers TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
