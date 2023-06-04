object1 = csvread('user1.txt');
object2 = csvread('attacker1.txt');
%illegal = csvread('illegal.txt');

%数据
X = [object1 ; object2];
%标签
Y = [ones(size(object1,1),1); -ones(size(object2,1),1) ];%-ones(size(illegal,1),1)];

% 生成随机的行索引,按照索引重新排列X和Y
idx = randperm(size(X, 1));
X = X(idx, :);
Y = Y(idx, :);

%划分数据集为训练集和测试集。将数据集划分为80%的训练集和20%的测试集
cv = cvpartition(size(X,1), 'HoldOut', 0.2);
idxTrain = training(cv); % 训练集的逻辑索引
idxTest = test(cv); % 测试集的逻辑索引
XTrain = X(idxTrain,:);
YTrain = Y(idxTrain);
XTest = X(idxTest,:);
YTest = Y(idxTest);

%标准化数据 使用zscore函数对训练集和测试集进行标准化处理
[XTrain, mu, sigma] = zscore(XTrain);
XTest = (XTest - repmat(mu, size(XTest,1), 1)) ./ repmat(sigma, size(XTest,1), 1);

%训练模型
SVMModel = fitcsvm(XTrain, YTrain, 'KernelFunction', 'rbf', 'BoxConstraint', 1, 'Standardize', true);


% 使用测试集进行测试
label = predict(SVMModel, XTest);
count = 0;
for i = 1:length(label)
    fprintf('label: %d, YTest: %d\n', label(i), YTest(i));
    if (label(i) == YTest(i))
        count = count + 1;
    end
end
fprintf('%f\n', count/length(label));
%disp(label);

% 5折交叉验证
verify = crossval(SVMModel, 'KFold', 5);

% 计算交叉验证性能指标，如分类错误率
error = kfoldLoss(verify);

fprintf('error: %f\n', error);


% newData = [212.80296,932.39246,739.3154,758.48303,0.65999997,0.65999997,564.3321,2.9240005,0.59454536];
% newData = (newData - mu) ./ sigma;
% disp(newData);
% newLabel = predict(SVMModel, newData);
% disp(newLabel);






