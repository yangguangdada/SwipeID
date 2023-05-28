function[Level1] = GetLevel1(audioIn,fs)
m = size(audioIn,1);
% Create and set up an audioFeatureExtractor object
extractor = audioFeatureExtractor("SampleRate",fs, ...
    "Window",hamming(m,"periodic"), ...  % 每帧m个采样点
    "OverlapLength",(0), ...  %  帧与帧之间有0个采样点重合
    "spectralCentroid",true,"spectralCrest",true, ...
    "spectralDecrease",true,"spectralEntropy",true, ...
    "spectralFlatness",true,"spectralRolloffPoint",true, ...
    "spectralSpread",true,"harmonicRatio",true);

% Extract parts of Level1 features from audio data
Level1 = extract(extractor,audioIn);

SpectralCentroid = Level1(:,1);
Crest = Level1(:,2);
Decrease = Level1(:,3);
Entropy = Level1(:,4);
Flatness = Level1(:,5);
RolloffPoint = Level1(:,6);
Spread = Level1(:,7);
HarmonicRatio = Level1(:,8);

% Spectral Centroid of Time-Domain Audio
