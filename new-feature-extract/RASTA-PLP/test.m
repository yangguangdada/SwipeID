 % Load a speech waveform
 [d,sr] = audioread('wav_Denoise/zmx-bot-Denoise.wav');
 % Look at its regular spectrogram
 subplot(411)
 specgram(d, 256, sr);
 
 % Calculate basic RASTA-PLP cepstra and spectra
 [cep, spec_feature] = rastaplp(d, sr);
 % .. and plot them
 subplot(412)
 imagesc(10*log10(spec_feature)); % Power spectrum, so dB is 10log10
 axis xy
 subplot(413)
 imagesc(cep)
 axis xy
 % Notice the auditory warping of the frequency axis to give more 
 % space to low frequencies and the way that RASTA filtering 
 % emphasizes the onsets of static sounds like vowels


 %% Calculate 12th order PLP features without RASTA
 % [cep2, spec2] = rastaplp(d, sr, 0, 12);
 % .. and plot them
 %subplot(414)
 %imagesc(10*log10(spec2));
 %axis xy
 % Notice the greater level of temporal detail compared to the 
 % RASTA-filtered version.  There is also greater spectral detail 
 % because our PLP model order is larger than the default of 8