import { Moment } from 'moment';
import { Status } from 'app/shared/model/enumerations/status.model';

export interface IPlateMap {
  id?: number;
  status?: Status;
  lastModified?: Moment;
  checksum?: string;
  activityName?: string;
  data?: string;
  numPlates?: number;
}

export class PlateMap implements IPlateMap {
  constructor(
    public id?: number,
    public status?: Status,
    public lastModified?: Moment,
    public checksum?: string,
    public activityName?: string,
    public data?: string,
    public numPlates?: number
  ) {}
}
