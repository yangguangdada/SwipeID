object1 = csvread('user1.txt');
object2 = csvread('attacker1.txt');
%illegal = csvread('illegal.txt');

%����
X = [object1 ; object2];
%��ǩ
Y = [ones(size(object1,1),1); -ones(size(object2,1),1) ];%-ones(size(illegal,1),1)];

% ���������������,����������������X��Y
idx = randperm(size(X, 1));
X = X(idx, :);
Y = Y(idx, :);

%�������ݼ�Ϊѵ�����Ͳ��Լ��������ݼ�����Ϊ80%��ѵ������20%�Ĳ��Լ�
cv = cvpartition(size(X,1), 'HoldOut', 0.2);
idxTrain = training(cv); % ѵ�������߼�����
idxTest = test(cv); % ���Լ����߼�����
XTrain = X(idxTrain,:);
YTrain = Y(idxTrain);
XTest = X(idxTest,:);
YTest = Y(idxTest);

%��׼������ ʹ��zscore������ѵ�����Ͳ��Լ����б�׼������
[XTrain, mu, sigma] = zscore(XTrain);
XTest = (XTest - repmat(mu, size(XTest,1), 1)) ./ repmat(sigma, size(XTest,1), 1);

%ѵ��ģ��
SVMModel = fitcsvm(XTrain, YTrain, 'KernelFunction', 'rbf', 'BoxConstraint', 1, 'Standardize', true);


% ʹ�ò��Լ����в���
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

% 5�۽�����֤
verify = crossval(SVMModel, 'KFold', 5);

% ���㽻����֤����ָ�꣬����������
error = kfoldLoss(verify);

fprintf('error: %f\n', error);


% newData = [212.80296,932.39246,739.3154,758.48303,0.65999997,0.65999997,564.3321,2.9240005,0.59454536];
% newData = (newData - mu) ./ sigma;
% disp(newData);
% newLabel = predict(SVMModel, newData);
% disp(newLabel);






