function features_aug= dataAugBehave(filename, dirpath)
    filepath = fullfile(dirpath,filename);
    features = csvread(filepath);
    idx = any(isnan(features),2);
    features = features(~idx, : );  %剔除含有nan的样本
    sigmas = std(features,1);
    num_raw_sample = size(features,1);
    num_feature = size(features,2);
    features_aug = zeros(num_raw_sample*8, num_feature);
%     for i =1:8
%         mat1 = randn(num_raw_sample,num_feature);
% %         for j = 1:num_feature
% %             mat1(: ,j) = mat1(:,j)*(sigmas(j)*0);  
% %         end
%         features_aug((i-1)*num_raw_sample+1:i*num_raw_sample,:) = mat1*0.0000001 + features;
% %     end
%     features_aug = [features;features_aug];
    features_aug = features;
end