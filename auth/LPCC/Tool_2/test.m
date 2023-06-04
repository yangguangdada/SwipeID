[Y1,fs1] = audioread("wav_Denoise/zyt-bot-Denoise.wav");
framed_signal = frames(Y1, 1024, 512, 0);
lpcc_feature = [];
for column_number = 1:1:size(framed_signal, 2)
    lpcc_feature = [lpcc_feature lpcc(framed_signal(:, column_number))'];
end
