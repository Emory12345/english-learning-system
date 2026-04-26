CREATE DATABASE IF NOT EXISTS english_learning;

USE english_learning;

-- 创建试卷表
CREATE TABLE IF NOT EXISTS paper (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    total_score INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    course_id BIGINT,
    teacher_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建题目表
CREATE TABLE IF NOT EXISTS question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    paper_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    options JSON,
    correct_answer JSON,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (paper_id) REFERENCES paper(id)
);

-- 插入2022年9月四级真题
INSERT INTO paper (title, total_score, type, course_id, teacher_id) VALUES ('2022年9月四级真题(第一套)', 710, 'CET4', 1, 1);

-- 插入写作题目
INSERT INTO question (paper_id, content, type, options, correct_answer) VALUES (1, 'Part I Writing (30 minutes)\nDirections: Suppose your class plans to invite some former graduates to share their job experiences with the students. As the organizer of the activity, you are now to write an invitation letter to the invited speakers on behalf of your class. You will have 30 minutes to write the letter. You should write at least 120 words.', 'writing', NULL, NULL);

-- 插入听力题目
INSERT INTO question (paper_id, content, type, options, correct_answer) VALUES (1, 'Part II Listening Comprehension (25 minutes)\nSection A\nDirections: In this section, you will hear three news reports. At the end of each news report, you will hear two or three questions. Both the news report and the questions will be spoken only once. After you hear a question, you must choose the best answer from the four choices marked A), B), C) and D). Then mark the corresponding letter on Answer Sheet 1 with a single line through the centre.', 'listening', '["1. A) It costs more than twice as much as a car ride. C) It symbolize", "2. A) International tourists. B) High-class travelers. D) C", "3. A) Treat her friends in a bar. C) B) Take a trip to Washington. D) Throw", "4. A) Spend no more than 30 dollars. C) Help B) Buy diﬀerent kinds of cheese. D) Find out", "5. A) It is the best kind of hard cheese. C) It B) It is the most popular in Spain. D) It is", "6. A) Buy what the man recommended. C) Choose one B) Have a taste of both of the cheeses. D) Ask the m", "7. A) New teachers and staﬀ have to be recruited. C) It involves B) It might take some time for students to adapt. D) The software ha", "8. A) It can greatly improve their learning eﬃciency. C) It can save t B) It can help them to interact more with teachers. D) It can develop t", "9. A) They may have trouble comprehending texts. C) They may pay le B) They may encounter technological problems. D) They may get distra", "10. A) It generates a great deal of electronic garbage. C) It emits hu B) It does a lot of damage to the environment. D) It accelerates", "11. A) It is an objective evaluation of a person’s character. C) It contribute B) It is a mental process inﬂuenced by many factors. D) It varies greatly a", "12. A) It can lead to incorrect judgments. C) It ca B) It can cause mistrust among people. D) It can give", "13. A) Their social skills were negatively aﬀected. C) Their emot B) Their school performance was slightly lower. D) Their relations w", "14. A) It may pose a threat to their children’s safety. C) It may hu B) It may aﬀect society’s traditional values. D) It may chang"]', '["B", "B", "D", "A", "D", "B", "B", "A", "D", "B", "B", "A", "C", "A"]');

-- 插入阅读题目
INSERT INTO question (paper_id, content, type, options, correct_answer) VALUES (1, 'Part III Reading Comprehension (40 minutes)\nSection A\nDirections: In this section, there is a passage with ten blanks. You are required to select one word for each blank from a list of choices given in a word bank following the passage. Read the passage through carefully before making your choices. Each choice in the bank is identiﬁed by a letter. Please mark the corresponding letter for each item on Answer Sheet 2 with a single line through the centre.\n\nWhile 92% of people believe others care most about fame and 26, fewer than 10% factor those qualities into their own success. This is according to the newly 27 study by Harvard Graduate School of Education professor Todd Smith. Smith says he was 28 by how past studies on success \"assumed what people will care about.\" In this study, his team \"went the 29 direction\" by spending years carrying out individual interviews and group surveys to see what people As a scientist, Smith 30 studied individuality for a living, and even he was surprised to ﬁnd younger respondents cared more about having a 31 in life. Those between the ages of 18 and 34 prioritized it most, and that prioritization dropped oﬀ as respondents’ ages went up. Perhaps this is because older people had fewer options when they were starting their careers, at a time when value Other trends included an emphasis on the importance of parenting. Being a parent 33 very high across the priorities of all study participants. Ultimately, Higher education institutions tend to focus on preparing students for high-paying jobs. For such institutions, from universities to workplaces, to better 35 people in the U.S., they’ll need to understand \"what the American public hig\"', 'reading', NULL, NULL);

-- 插入翻译题目
INSERT INTO question (paper_id, content, type, options, correct_answer) VALUES (1, 'Part IV Translation (30 minutes)\nDirections: For this part, you are allowed 30 minutes to translate a passage from Chinese into English. You should write your answer on Answer Sheet 2.\n\n戏曲是一种中国传统的艺术形式，可以追溯到唐朝。中国戏曲吸引观众的一大特色是其独具风格的 脸谱 (facial painting)。脸谱代表不同角色的性格和命运。观众通过观察脸谱能 够更好地理解这些角色的 故事。欣赏戏曲是中国人特别是老年人的一大乐趣。为了吸引更 多的年轻观众，传统戏曲正在不断地发 展和创新。如今，越来越多的外国观众也喜欢中国', 'translation', NULL, NULL);
