rs.initiate();
cfg = rs.conf();
cfg.members[0].host = _getEnv('HOSTNAME') + ':27017'
rs.reconfig(cfg, {"force": true});