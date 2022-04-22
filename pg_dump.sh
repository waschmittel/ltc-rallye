#!/bin/bash
PGPASSWORD="ltc" pg_dump -U rallye -h localhost -p 54320 rallye > ~/Downloads/rallye/rally-dump-`date +%Y-%m-%d_%H-%M-%S`.sql
