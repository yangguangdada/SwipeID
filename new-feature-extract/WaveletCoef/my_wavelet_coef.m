function WaveletCoef = my_wavelet_cof(signal)

wtecg = modwt(signal);
% [r,c] = size(A);

[WaveletCoef, i] = max(wtecg,[], 2);

WaveletCoef = WaveletCoef(1:12,1)';
end

