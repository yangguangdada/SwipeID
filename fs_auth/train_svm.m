

% 加载训练数据集
positive_name = 'yjh';
train_dataset = [positive_name '.mat'];
load(train_dataset)
X = T1(:,1:99);
Y = T1(:,100);





