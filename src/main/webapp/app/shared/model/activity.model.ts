import { IPlateMap } from 'app/shared/model/plate-map.model';

export interface IActivity {
  id?: number;
  name?: string;
  description?: string;
  platemaps?: IPlateMap[];
}

export class Activity implements IActivity {
  constructor(public id?: number, public name?: string, public description?: string, public platemaps?: IPlateMap[]) {}
}
