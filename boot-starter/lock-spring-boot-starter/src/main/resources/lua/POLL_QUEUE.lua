local expiredValues = redis.call('zrangebyscore', KEYS[1], ARGV[1], ARGV[2], 'limit', ARGV[3], ARGV[4]);
if tonumber(ARGV[1])>0 then
	redis.call('zremrangebyscore', KEYS[1], 0, ARGV[1]);
end
if #expiredValues > 0 then
    redis.call('zrem', KEYS[1], unpack(expiredValues));
	return expiredValues;
end
return nil;