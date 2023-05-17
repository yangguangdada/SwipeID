function [Flux,Kurtosis,Skewness,Slope] = get_spectral_statistics(signal, fs)
m = size(signal,1);
% Create and set up an audioFeatureExtractor object
extractor = audioFeatureExtractor("SampleRate",fs, ...
    "Window",hamming(m,"periodic"), ...  % 每帧m个采样点
    "OverlapLength",(0), ...  %  帧与帧之间有0个采样点重合
    "spectralFlux",true,"spectralKurtosis",true, ...
    "spectralSkewness",true,"spectralSlope",true);

% Extract features from audio data
features = extract(extractor,signal);
Flux = features(:,1);
Kurtosis = features(:,2);
Skewness = features(:,3);
Slope = features(:,4);

