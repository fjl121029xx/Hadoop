## 建表
DROP TABLE IF EXISTS `xiaoyuer`;
CREATE TABLE `xiaoyuer` (
  `ss` bigint(20) DEFAULT NULL,
  `paytime` bigint(20) DEFAULT NULL,
  `userid` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

## 存储函数
DELIMITER $$
DROP PROCEDURE IF EXISTS xiaoyuer$$

CREATE PROCEDURE xiaoyuer ()

BEGIN
	DECLARE uid	varchar(20) DEFAULT '';
	DECLARE ptime	BIGINT DEFAULT 0;

	DECLARE done INT DEFAULT 0;
	DECLARE edone INT DEFAULT 0;

	BEGIN

		DECLARE userId_list CURSOR FOR SELECT distinct userId FROM  v_huatu_order2 ;
		DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1 ;

		OPEN userId_list ;

			vkfdatasLoop:LOOP

				FETCH next from userId_list INTO uid ;
				IF done  = 1 THEN
					LEAVE vkfdatasLoop;#跳出循环
				ELSE
				SET edone = 0;
					BEGIN

						DECLARE paytime_list CURSOR FOR SELECT payTime FROM  v_huatu_order2 a where a.userId = uid order by payTime ASC;
						DECLARE CONTINUE HANDLER FOR NOT FOUND SET edone = 1;#结束标识

						OPEN paytime_list;
							vfiledataLoop:LOOP
								FETCH next from paytime_list INTO ptime ;
									IF edone = 1 THEN
										LEAVE vfiledataLoop;
									ELSE
										INSERT INTO xiaoyuer select SUM(moneyReceipt) ss,MAX(payTime) as paytime,userId as userid from v_huatu_order2 where userId=uid and payTime <= ptime ;
									END IF;
							END LOOP;
						CLOSE paytime_list;
					END;
				END IF;
			END LOOP;

		CLOSE userId_list;
		COMMIT;
	END;
END;
