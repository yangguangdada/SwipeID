
load('models\lmy\lmy_model_0.mat');

val_names = dir('as\val\*.wav');
val_features = {};

val_features = Feature_extra(val_names,val_features,'as\val','lmy');
val_X = cell2mat(val_features(:,1:99));
val_Y = cell2mat(val_features(:,100));

[labels,scores] = predict(svm_model_0 , val_X);

disp('true_labels');
disp(val_Y);
disp('pred_labels');
disp(labels);
disp('scores');
disp(scores);










