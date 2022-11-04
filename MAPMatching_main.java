        #region
        /// <summary>
        /// 运行
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void btnRun_ItemClick(object sender, ItemClickEventArgs e)
        {
            //输入点图层的名称
            IFeatureLayer ptLayer = queryFeatureLayer("测试地图匹配数据.shp");

            IFeatureClass ptFeatClass = ptLayer.FeatureClass;
            IFeatureCursor featureCursor = featureCursor = ptFeatClass.Search(null, false);
            IFeature feature = featureCursor.NextFeature();
            IPoint point = new PointClass();

            while (feature != null)
            {
                point = feature.Shape as IPoint;
                //查找离点最近的线
                IFeature nearFea = GetNearestFeature(point, 1.2);
                IPoint neaPoint = GetNearestPoint(point, nearFea);

                neaPoint.Z = point.Z;
                //移动点

                MoveToNeaPoint(feature, point, neaPoint);

                if (featureCursor == null || feature == null)
                {
                    return;
                }
                feature = featureCursor.NextFeature();

            }
            MessageBox.Show("ok--");
        }


        #region 方法封装

        //查询给定名称的图层

        private IFeatureLayer queryFeatureLayer(string name)
        {

            for (int i = 0; i < mainMapControl.LayerCount; i++)
            {
                ILayer layer = mainMapControl.get_Layer(i);
                if (layer.Name.Equals(name))
                {
                    return (IFeatureLayer)layer;
                }
            }
            return null;
        }
        ////得到指定图层上距point最近的feature上的最近点

        public IPoint GetNearestPoint(IPoint point, IFeature nearFea)
        {

            IProximityOperator Proximity = (IProximityOperator)point;

             // 输入路网图层名称
            IFeatureLayer FeaLyr = queryFeatureLayer("青岛市路网.shp");

            IFeatureClass FeaCls = FeaLyr.FeatureClass;

            IQueryFilter queryFilter = null;

            ITopologicalOperator topoOper = (ITopologicalOperator)point;

            IGeometry geo = topoOper.Buffer(1.2);

            ISpatialFilter sf = new SpatialFilterClass();

            sf.Geometry = geo;

            sf.GeometryField = FeaCls.ShapeFieldName;

            sf.SpatialRel = esriSpatialRelEnum.esriSpatialRelCrosses;





            IFeatureCursor FeaCur = FeaCls.Search(queryFilter, false);

            IFeature Fea = nearFea = FeaCur.NextFeature();

            double minDistince, Distance;

            if (Fea == null)

                return null;

            minDistince = Distance = Proximity.ReturnDistance((IGeometry)Fea.Shape);    //最近的距离值

            //保存距离最近的feature

            Fea = FeaCur.NextFeature();

            while (Fea != null)
            {

                Distance = Proximity.ReturnDistance((IGeometry)Fea.Shape);

                if (Distance < minDistince)
                {

                    minDistince = Distance;

                    nearFea = Fea;

                }

                Fea = FeaCur.NextFeature();

            }   //end while

            Proximity = (IProximityOperator)nearFea.Shape;

            return Proximity.ReturnNearestPoint(point, esriSegmentExtension.esriNoExtension);

        }



        //查找最近的线

        public IFeature GetNearestFeature(IPoint p, double rongcha)
        {

            IFeature nearFea;

            IProximityOperator Proximity = (IProximityOperator)p;
             // 输入路网图层名称
            IFeatureLayer FeaLyr = queryFeatureLayer("青岛市路网.shp");

            IFeatureClass FeaCls = FeaLyr.FeatureClass;

            IQueryFilter queryFilter = null;

            IFeatureCursor FeaCur = FeaCls.Search(queryFilter, false);

            IFeature Fea = nearFea = FeaCur.NextFeature();

            double minDistince, Distance;

            if (Fea == null)

                return null;

            minDistince = Distance = Proximity.ReturnDistance((IGeometry)Fea.Shape);    //最近的距离值

            //保存距离最近的feature

            Fea = FeaCur.NextFeature();

            while (Fea != null)
            {

                Distance = Proximity.ReturnDistance((IGeometry)Fea.Shape);
                if (Distance < minDistince)
                {

                    minDistince = Distance;

                    nearFea = Fea;
                }
                Fea = FeaCur.NextFeature();
            }   //end while
            return nearFea;
        }

        private void MoveToNeaPoint(IFeature feature, IPoint point, IPoint neaPoint)
        {
            IGeometry toGeometry = null;
            IPoint geomType = new PointClass();
            feature.Shape = neaPoint as IGeometry;
            feature.Store();
        }

        #endregion
        #endregion