function [GTCC] = get_GTCC(signal,fs)

cepFeatures = cepstralFeatureExtractor('FilterBank','Gammatone');
m = size(signal,1);
frames = buffer(signal,m,0,"nodelay");

[GTCC,delta,deltaDelta] = cepFeatures(frames); 

end

