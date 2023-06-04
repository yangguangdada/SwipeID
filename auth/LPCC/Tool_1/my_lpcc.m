function [ lpcc_coeff ] = my_lpcc(signal, fs)


% Delete one of the stereo channel and transpose.
mono_signal = signal(:, 1);

% Process signal into frames.
framed_signal = frames(mono_signal, 1024, 512, 0);

% Compute LPC coefficients.
lpc_coeff = lpc_(framed_signal,12);%������Ԥ������Ľ״θ�ֵ

% Compute LPCC coefficients
lpcc_coeff = lpcc(lpc_coeff, 6);

end

