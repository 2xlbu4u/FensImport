-- Xena File import into table
--DELETE FROM dbo.xena
COPY dbo.xena
FROM '/tmp/Xena_TR15_Re-Run_T-Mobile Test_PW_Sierra_20210407_085934xena_db_import.csv'
DELIMITER ',' CSV HEADER;
--SELECT * from dbo.xena
-- SELECT count(1) from dbo.xena

-- Histogram Latency File import into table
--DELETE FROM dbo.xena_latency_hist
COPY dbo.xena_latency_hist
FROM '/tmp/Xena_TR15_P7_Latency_Re-Run_T-Mobile Test_PW_Sierra_20210407_085934_latency_histo_db_import.csv'
DELIMITER ',' CSV HEADER;
--SELECT * from dbo.xena_latency_hist

-- Histogram Jitter File import into table
--DELETE FROM dbo.xena_jitter_hist
COPY dbo.xena_jitter_hist
FROM '/tmp/Xena_TR15_P7_Jitter_Re-Run_T-Mobile Test_PW_Sierra_20210407_085934_jitter_histo_db_import.csv'
DELIMITER ',' CSV HEADER;
--SELECT * from dbo.xena_jitter_hist

-- Wan Quality File import into table
--DELETE FROM dbo.wan_quality
COPY dbo.wan_quality
FROM '/tmp/wan_quality_P6_Cellular 2_2021-04-07_db_import.csv'
DELIMITER ',' CSV HEADER;
-- SELECT * from dbo.wan_quality

-- Chariot Mix File import into table
--DELETE FROM dbo.wan_quality
COPY dbo.chariot_mix
FROM '/tmp/     _P6_Cellular 2_2021-04-07_db_import.csv'
DELIMITER ',' CSV HEADER;
-- SELECT * from dbo.chariot_mix

-- Cable Service File import into table
--DELETE FROM dbo.cable_service
COPY dbo.cable_service
FROM '/tmp/Teresa-ping-test-4-24_db_import.csv'
DELIMITER ',' CSV HEADER;
-- SELECT * from dbo.cable_service where testname like 'Teresa%'