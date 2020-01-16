import { Moment } from 'moment';
import { IActivity } from 'app/shared/model/activity.model';
import { Status } from 'app/shared/model/enumerations/status.model';

export interface IPlateMap {
  id?: number;
  status?: Status;
  lastModified?: Moment;
  checksum?: string;
  data?: string;
  activity?: IActivity;
}

export class PlateMap implements IPlateMap {
  constructor(
    public id?: number,
    public status?: Status,
    public lastModified?: Moment,
    public checksum?: string,
    public data?: string,
    public activity?: IActivity
  ) {}
}
